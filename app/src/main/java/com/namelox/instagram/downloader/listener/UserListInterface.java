package com.namelox.instagram.downloader.listener;

import com.namelox.instagram.downloader.api.model.NodeModel;
import com.namelox.instagram.downloader.api.model.TrayModel;

public interface UserListInterface {
    void FacebookUserListClick(int i, NodeModel nodeModel);

    void FacebookUserListClick(int i, TrayModel trayModel);
}