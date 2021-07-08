package com.pilates.app.model.dto;

public class SaveGalleryResponseDto extends StatusMessageDto {
    private Integer id;
    private String path;
    private boolean loading;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
