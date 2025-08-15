package com.sahabatquran.controller;

import com.sahabatquran.domain.Permission;
import com.sahabatquran.service.PermissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("permissions", permissionService.findAll());
        return "admin/permissions/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("permission", new Permission());
        return "admin/permissions/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Permission permission) {
        permissionService.save(permission);
        return "redirect:/admin/permissions";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("permission", permissionService.findById(id));
        return "admin/permissions/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, @ModelAttribute Permission permission) {
        permission.setId(id);
        permissionService.save(permission);
        return "redirect:/admin/permissions";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id) {
        permissionService.deleteById(id);
        return "redirect:/admin/permissions";
    }
}
