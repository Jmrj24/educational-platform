package com.example.demo.role.dto;

import com.example.demo.role.NameRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDTO {
    private NameRole role;
    private Set<Long> permissionsListIds;
}