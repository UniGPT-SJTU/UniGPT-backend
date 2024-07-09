package com.ise.unigpt.model;

import java.util.List;

import com.ise.unigpt.dto.PluginCreateDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "plugin")
public class Plugin {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    // list of parameters
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "plugin")
    private List<Parameter> parameters;

    // the url of the tool
    @Column(name = "url", nullable = false)
    private String url;

    // the user who created the tool
    @ManyToOne()
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // the url of the avatar
    @Column(name = "avatar")
    private String avatar;

    // the urls of the photos
    @Column(name = "photos")
    private List<String> photos;

    // the detail of the tool
    @Column(name = "detail", length = 3000)
    private String detail;

    // boolean is published
    @Column(name = "is_published")
    private Boolean isPublished;

    // 哪些机器人使用了这个插件
    @ManyToMany(mappedBy = "plugins")
    private List<Bot> bots;

    // constructor
    public Plugin(String name, String description, List<Parameter> parameters, String url, User creator, String avatar, List<String> photos, String detail, Boolean isPublished) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.url = url;
        this.creator = creator;
        this.avatar = avatar;
        this.photos = photos;
        this.detail = detail;
        this.isPublished = isPublished;
    }

    // constructor
    public Plugin(PluginCreateDTO dto, User user, String url) {
        this.name = dto.getName();
        this.description = dto.getDescription();

        this.url = url;
        this.creator = user;
        this.avatar = dto.getAvatar();
        this.photos = dto.getPhotos();
        this.detail = dto.getDetail();
        this.isPublished = dto.getIsPublished();
        // for parameters
        this.parameters = dto.getParameters().stream().map(p -> new Parameter(p, this)).toList();
    }

    // constructor
    public Plugin() {
        // not used
    }
}
