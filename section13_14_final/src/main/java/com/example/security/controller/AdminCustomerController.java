package com.example.security.controller;

import com.example.security.entity.Customer;
import com.example.security.service.MemberService;
import com.example.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public String showCustomerList(Model model) {
        List<Customer> customers = memberService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "admin/customerList";
    }

    @PostMapping("/update-permissions")
    public String updateCustomerPermissions(@RequestParam Long customerId,
                                            @RequestParam String newPermissions) {
        // Implement logic to update customer permissions
        //1.USER -> ROLE_MANAGER , ROLE_ADMIN
        roleService.updateCustomerPermissions(customerId,newPermissions); //권한 업데이트
        return "redirect:/admin/customers";
    }

    @PostMapping("/add-permissions")
    public String addCustomerPermissions(@RequestParam Long customerId,
                                            @RequestParam String newPermissions) {
        // Implement logic to update customer permissions
        //1.USER -> ROLE_MANAGER , ROLE_ADMIN
        roleService.addCustomerPermissions(customerId,newPermissions); //권한추가
        return "redirect:/admin/customers";
    }

}
