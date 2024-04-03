package com.tester.notes.entities;

import java.io.Serializable;
import java.util.Map;

public class Repository implements Serializable {
    private int id;

    public Owner getOwner() {
        return owner;
    }

    private Owner  owner;

    public String getName() {
        return name;
    }

    private String name;

    public String getFull_name() {
        return full_name;
    }

    private String full_name;
    private String description;
    private boolean empty;
    private boolean is_private;
    private boolean fork;
    private boolean is_template;
    private Object parent;
    private boolean mirror;
    private int size;
    private String language;
    private String languages_url;
    private String html_url;
    private String url;
    private String link;
    private String ssh_url;
    private String clone_url;
    private String original_url;
    private String website;
    private int stars_count;
    private int forks_count;
    private int watchers_count;
    private int open_issues_count;
    private int open_pr_counter;
    private int release_counter;
    private String default_branch;
    private boolean archived;
    private String created_at;
    private String updated_at;
    private String archived_at;
    private Map<String, Boolean> permissions;
    private boolean has_issues;
    private Map<String, Boolean> internal_tracker;
    private boolean has_wiki;
    private boolean has_pull_requests;
    private boolean has_projects;
    private boolean has_releases;
    private boolean has_packages;
    private boolean has_actions;
    private boolean ignore_whitespace_conflicts;
    private boolean allow_merge_commits;
    private boolean allow_rebase;
    private boolean allow_rebase_explicit;
    private boolean allow_squash_merge;
    private boolean allow_rebase_update;
    private boolean default_delete_branch_after_merge;
    private String default_merge_style;
    private boolean default_allow_maintainer_edit;
    private String avatar_url;
    private boolean internal;
    private String mirror_interval;
    private String mirror_updated;
    private Object repo_transfer;

    public static class Owner implements Serializable{
        private int id;
        private String login;
        private String login_name;
        private String full_name;
        private String email;
        private String avatar_url;
        private String language;
        private boolean is_admin;
        private String last_login;
        private String created;
        private boolean restricted;
        private boolean active;
        private boolean prohibit_login;
        private String location;
        private String website;
        private String owner_description;
        private String visibility;
        private int followers_count;
        private int following_count;
        private int starred_repos_count;
        private String username;
        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "Owner{" +
                    "id=" + id +
                    ", login='" + login + '\'' +
                    ", login_name='" + login_name + '\'' +
                    ", full_name='" + full_name + '\'' +
                    ", email='" + email + '\'' +
                    ", last_login='" + last_login + '\'' +
                    ", created='" + created + '\'' +
                    ", owner_description='" + owner_description + '\'' +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", description='" + description + '\'' +
                ", empty=" + empty +
                ", is_private=" + is_private +
                ", size=" + size +
                ", html_url='" + html_url + '\'' +
                ", url='" + url + '\'' +
                ", link='" + link + '\'' +
                ", ssh_url='" + ssh_url + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
