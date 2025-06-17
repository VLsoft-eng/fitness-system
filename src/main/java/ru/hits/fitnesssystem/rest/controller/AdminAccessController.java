package ru.hits.fitnesssystem.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.UserService;
import ru.hits.fitnesssystem.rest.model.ChangeUserRoleDto;

@RequestMapping("/admin-access")
@RequiredArgsConstructor
@RestController()
public class AdminAccessController {

    private final UserService userService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/change-user-role")
    public void changeUserRole(@RequestBody ChangeUserRoleDto changeUserRoleDto) {
        userService.changeUserRole(changeUserRoleDto);
    }
}
