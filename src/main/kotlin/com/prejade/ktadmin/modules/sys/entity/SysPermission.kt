package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.modules.sys.enumes.SysPermissionType
import javax.persistence.*

@Entity
class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @Column(unique = true, nullable = false)
    lateinit var label: String

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var type: SysPermissionType = SysPermissionType.MENU

    /**
     * 权限删除后，对应的权限与角色的关系也要删除
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    var roles: Set<SysRole> = setOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: SysPermission? = null

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = [JoinColumn(name = "sys_permission_id")],
        inverseJoinColumns = [JoinColumn(name = "sys_role_id")]
    )
    var permissions: Set<SysRole> = setOf()
}
