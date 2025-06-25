package com.github.johypark97.varchivemacro.macro.common.license.infra.repository;

import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultOpenSourceLicenseRepository implements OpenSourceLicenseRepository {
    private final Map<String, License> licenseMap = new HashMap<>();

    @Override
    public void save(License value) {
        licenseMap.put(value.libraryName(), value);
    }

    @Override
    public List<String> findAllLibrary() {
        return licenseMap.keySet().stream().toList();
    }

    @Override
    public License findLicense(String name) {
        return licenseMap.get(name);
    }
}
