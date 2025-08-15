package com.sahabatquran.controller;

import com.sahabatquran.domain.Role;
import com.sahabatquran.service.PermissionService;
import com.sahabatquran.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "admin/roles/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("permissions", permissionService.findAll());
        return "admin/roles/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Role role) {
        roleService.save(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("role", roleService.findById(id));
        model.addAttribute("permissions", permissionService.findAll());
        return "admin/roles/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, @ModelAttribute Role role) {
        role.setId(id);
        roleService.save(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id) {
        roleService.deleteById(id);
        return "redirect:/admin/roles";
    }
}
