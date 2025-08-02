package org.shoppingdashboard.Service;

import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Repository.BrandingSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrandingService {

    @Autowired
    private BrandingSettingsRepository repository;

    public BrandingSettings getSettings() {
        return repository.findById(1L).orElse(new BrandingSettings());
    }

    public void saveSettings(BrandingSettings settings) {
        settings.setId(1L); // Ensure single record for branding settings
        repository.save(settings);
    }
}