package com.ardi.afarensis.entity

import com.ardi.afarensis.dto.Role
import jakarta.persistence.*

@Table(name = "users_roles")
@Entity
class UserRole(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    @ManyToOne
    @JoinColumn(name = "usersPk", insertable = true, updatable = true, nullable = false)
    var user: User
) {}