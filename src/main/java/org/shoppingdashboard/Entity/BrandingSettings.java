package org.shoppingdashboard.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Getter
@Setter
public class BrandingSettings {
    @Id
    private Long id;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String fontFamily;
    @Column(length = 65535, columnDefinition = "TEXT")
    private String customHtml;
}