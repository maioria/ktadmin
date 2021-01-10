package com.prejade.ktadmin.modules.sys.entity

import com.prejade.ktadmin.modules.sys.enumes.SysPermissionType
import javax.persistence.*

@Entity
class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(unique = true, nullable = false, length = 100)
    lateinit var name: String

    @Column(unique = true, nullable = false, length = 100)
    lateinit var label: String

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var type: SysPermissionType = SysPermissionType.MENU

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: SysPermission? = null
}
