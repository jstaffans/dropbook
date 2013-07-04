package com.mysema.dropbook;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import io.ifar.dropwizard.shiro.ShiroConfiguration;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DropbookConfiguration extends Configuration implements AssetsBundleConfiguration {

    @Valid
    @NotEmpty
    @JsonProperty
    @Getter
    private List<String> quotes;

    @Valid
    @NotNull
    @JsonProperty
    @Getter
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @Valid
    @NotNull
    @JsonProperty("shiro_configuration")
    @Getter
    private ShiroConfiguration shiro = new ShiroConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private AssetsConfiguration assets;

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;

    }
}

