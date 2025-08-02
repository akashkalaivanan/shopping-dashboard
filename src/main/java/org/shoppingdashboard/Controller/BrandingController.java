package org.shoppingdashboard.Controller;

import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Service.BrandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/admin/branding")
public class BrandingController {

    @Autowired
    private BrandingService brandingService;

    @GetMapping
    public String showBrandingPage(Model model) {
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        return "admin_branding";
    }

    @PostMapping("/save")
    public String saveBrandingSettings(
            @RequestParam("logo") MultipartFile logoFile,
            @ModelAttribute BrandingSettings settings) throws IOException {
        if (!logoFile.isEmpty()) {
            String externalFolderPath = "C:/Users/dell/Desktop/shop/Shopping-dashboard/logo/";
            File folder = new File(externalFolderPath);

            // Create the folder if it does not exist
            if (!folder.exists() && !folder.mkdirs()) {
                throw new RuntimeException("Failed to create folder: " + externalFolderPath);
            }

            // Save the image file
            String fileName = logoFile.getOriginalFilename();
            File file = new File(externalFolderPath + fileName);
            logoFile.transferTo(file);

            // Set the relative path to the logo URL
            settings.setLogoUrl("/logo/" + fileName);
        }

        brandingService.saveSettings(settings);
        return "redirect:/admin/branding";
    }
}