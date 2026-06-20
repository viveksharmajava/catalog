package com.playpro.playpro.catalog.entity.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "prod_catalog")
public class ProdCatalog {

    @Id
    @Column(name = "prod_catalog_id", length = 20)
    private String prodCatalogId;

    @Column(name = "catalog_name", nullable = false, length = 100)
    private String catalogName;

    @Column(name = "use_quick_add", length = 1)
    private String useQuickAdd = "Y";

    @Column(name = "style_sheet", length = 250)
    private String styleSheet;

    @Column(name = "header_logo", length = 250)
    private String headerLogo;

    @Column(name = "content_path_prefix", length = 255)
    private String contentPathPrefix;

    @Column(name = "template_path_prefix", length = 255)
    private String templatePathPrefix;

    @Column(name = "view_allow_perm_reqd", length = 1)
    private String viewAllowPermReqd = "N";

    @Column(name = "purchase_allow_perm_reqd", length = 1)
    private String purchaseAllowPermReqd = "N";

    public String getProdCatalogId() {
        return prodCatalogId;
    }

    public void setProdCatalogId(String prodCatalogId) {
        this.prodCatalogId = prodCatalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getUseQuickAdd() {
        return useQuickAdd;
    }

    public void setUseQuickAdd(String useQuickAdd) {
        this.useQuickAdd = useQuickAdd;
    }

    public String getStyleSheet() {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    public String getHeaderLogo() {
        return headerLogo;
    }

    public void setHeaderLogo(String headerLogo) {
        this.headerLogo = headerLogo;
    }

    public String getContentPathPrefix() {
        return contentPathPrefix;
    }

    public void setContentPathPrefix(String contentPathPrefix) {
        this.contentPathPrefix = contentPathPrefix;
    }

    public String getTemplatePathPrefix() {
        return templatePathPrefix;
    }

    public void setTemplatePathPrefix(String templatePathPrefix) {
        this.templatePathPrefix = templatePathPrefix;
    }

    public String getViewAllowPermReqd() {
        return viewAllowPermReqd;
    }

    public void setViewAllowPermReqd(String viewAllowPermReqd) {
        this.viewAllowPermReqd = viewAllowPermReqd;
    }

    public String getPurchaseAllowPermReqd() {
        return purchaseAllowPermReqd;
    }

    public void setPurchaseAllowPermReqd(String purchaseAllowPermReqd) {
        this.purchaseAllowPermReqd = purchaseAllowPermReqd;
    }
}
