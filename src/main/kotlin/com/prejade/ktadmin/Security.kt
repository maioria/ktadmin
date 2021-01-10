package com.prejade.ktadmin

import com.fasterxml.jackson.databind.ObjectMapper
import com.prejade.ktadmin.modules.sys.model.DataPermissionModel
import com.prejade.ktadmin.modules.sys.service.SysUserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import java.io.Serializable
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author tao
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(jwtAuthenticationProvider)
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(*SysConstant.getNotLoginUrls()).permitAll()
            .anyRequest().authenticated()
        http.logout().logoutSuccessHandler { _, httpServletResponse, _ ->
            httpServletResponse.status = HttpStatus.OK.value()
            httpServletResponse.writer.flush()
        }
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(authenticationManager())
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }
}

class JwtAuthenticationFilter(authenticationManager: AuthenticationManager) :
    BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        SecurityUtils.checkAuthentication(request)
        chain.doFilter(request, response)
    }
}

@Component
class JwtAuthenticationProvider
    : DaoAuthenticationProvider() {
    override fun additionalAuthenticationChecks(
        userDetails: UserDetails?,
        authentication: UsernamePasswordAuthenticationToken?
    ) {
        val credentials = authentication!!.credentials.toString()
        if (userDetails is JwtUserDetails) {
            val salt = userDetails.salt
            if (PasswordUtils.matches(credentials, salt, userDetails.password)) {
                return
            }
        }
        throw BadCredentialsException(
            messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"
            )
        )
    }

    @Autowired
    override fun setUserDetailsService(userDetailsService: UserDetailsService?) {
        super.setUserDetailsService(userDetailsService)
    }
}

@Component
class UserDetailsService(val sysUserService: SysUserService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = sysUserService.getByUsername(username)!!
        return JwtUserDetails(
            user.id!!, user.username, user.password,
            user.salt, user.getPermissionNames().map { SimpleGrantedAuthority(it) },
            sysUserService.getDataPermissionModel(user)
        )
    }

}

class SecurityUtils {
    companion object {
        fun login(
            request: HttpServletRequest,
            username: String,
            password: String,
            authenticationManager: AuthenticationManager
        ): JwtAuthenticationToken {
            val token = JwtAuthenticationToken(username, password)
            token.details = WebAuthenticationDetailsSource().buildDetails(request)
            val authentication: Authentication = authenticationManager.authenticate(token)
            SecurityContextHolder.getContext().authentication = authentication
            token.token = JwtTokenUtils.generateToken(authentication)
            return token
        }

        fun checkAuthentication(request: HttpServletRequest) {
            val authentication = JwtTokenUtils.getAuthenticationFromToken(request)
            SecurityContextHolder.getContext().authentication = authentication
        }

        fun getLoginUser(authentication: Authentication?): JwtUserDetails {
            val principal = authentication?.principal
            if (principal is JwtUserDetails) {
                return principal
            }
            throw NullPointerException(principal.toString())
        }

        fun getAuthentication(): Authentication? {
            return SecurityContextHolder.getContext()?.authentication
        }

        fun getLoginUser(): JwtUserDetails {
            val authentication: Authentication = getAuthentication()!!
            return getLoginUser(authentication)
        }
    }
}

class JwtTokenUtils : Serializable {
    companion object {
        private const val USERNAME: String = Claims.SUBJECT
        private const val CREATED: String = "created"
        private const val AUTHORITIES: String = "authorities"
        private const val DATA_AUTHORITIES: String = "data_authorities"
        private const val ID: String = "id"
        private const val SECRET: String = "abide"
        const val EXPIRE_TIME: Long = 12 * 60 * 60 * 1000
        private val objectMapper = ObjectMapper()
        fun generateToken(authentication: Authentication): String {
            val loginUser = SecurityUtils.getLoginUser(authentication)
            val claims: Map<String, Any?> = mapOf(
                ID to loginUser.id, USERNAME to loginUser.username,
                CREATED to Date(), AUTHORITIES to authentication.authorities,
                DATA_AUTHORITIES to objectMapper.writeValueAsString(loginUser.dataPermission)
            )
            return generateToken(claims)
        }

        private fun generateToken(claims: Map<String, Any?>): String {
            val expirationDate = Date(System.currentTimeMillis() + EXPIRE_TIME)
            return Jwts.builder().setClaims(claims).setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET).compact()
        }

        /**
         * 获取到token进行校验
         */
        fun getAuthenticationFromToken(request: HttpServletRequest): Authentication? {
            val token = getToken(request)
            if (token != null) {
                if (SecurityUtils.getAuthentication() == null) {
                    val claims = getClaimsFromToken(token) ?: return null
                    val username = claims.subject ?: return null
                    if (isTokenExpired(token)) {
                        return null
                    }
                    val authors = claims[AUTHORITIES]
                    val id: Int = claims[ID] as Int
                    val authorities = mutableListOf<GrantedAuthority>()
                    if (authors is List<*>) {
                        for (o in authors) {
                            if (o is Map<*, *>) {
                                authorities.add(GrantedAuthorityImpl(o["authority"] as String))
                            }
                        }
                    }
                    return JwtAuthenticationToken(
                        JwtUserDetails(
                            id,
                            username,
                            "",
                            "",
                            authorities,
                            objectMapper.readValue(claims[DATA_AUTHORITIES] as String, DataPermissionModel::class.java)
                        ), null, authorities, token
                    )
                } else {
                    if (validateToken(token, SecurityUtils.getLoginUser().username)) {
                        return SecurityUtils.getAuthentication()
                    }
                }
            }
            return null
        }

        private fun validateToken(token: String, username: String?): Boolean {
            val curUsername = getUsernameFromToken(token)
            return curUsername == username && !isTokenExpired(token)
        }

        private fun getUsernameFromToken(token: String): String? {
            return getClaimsFromToken(token)?.subject
        }

        fun getToken(request: HttpServletRequest): String? {
            var token = request.getHeader("Authorization")
            val tokenHead = "Bearer "
            if (token == null) {
                token = request.getHeader("token")
                if (token == null) {
                    token = request.getHeader("Access-Token")
                    if (token == null) token = request.getParameter("token")
                }
            } else if (token.contains(tokenHead)) {
                token = token.substring(tokenHead.length)
            }
            if ("" == token) {
                token = null
            }
            return token
        }

        private fun isTokenExpired(token: String): Boolean {
            val claims = getClaimsFromToken(token)
            val expiration = claims!!.expiration
            return expiration.before(Date())
        }

        private fun getClaimsFromToken(token: String): Claims? {
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).body
        }
    }
}

class PasswordUtils {
    companion object {
        fun matches(password: String, salt: String, encPassword: String): Boolean {
            val castPass = encode(password, salt)
            return castPass == encPassword
        }

        fun encode(password: String, salt: String): String {
            return DigestUtils.md5DigestAsHex(("$password-$salt").toByteArray())
        }

        fun getSalt(): String {
            return UUID.randomUUID().toString().replace("-".toRegex(), "").substring(0, 20)
        }
    }
}

class JwtUserDetails(
    val id: Int, private val username: String, private val password: String,
    val salt: String, private val authorities: Collection<GrantedAuthority>,
    val dataPermission: DataPermissionModel
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}

class JwtAuthenticationToken : UsernamePasswordAuthenticationToken {
    var token: String? = null

    constructor(principal: Any?, credentials: Any?) : super(principal, credentials)
    constructor(principal: Any?, credentials: Any?, authorities: Collection<GrantedAuthority>, token: String) : super(
        principal,
        credentials,
        authorities
    ) {
        this.token = token
    }

}

class GrantedAuthorityImpl(private val authorityVal: String) : GrantedAuthority {
    override fun getAuthority(): String {
        return authorityVal
    }
}
