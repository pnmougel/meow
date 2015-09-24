#!/usr/bin/gjs

imports.searchPath.unshift('.');

const Server = imports.server;
const Entries = imports.entries;

Server.registerHandler("list-app", Entries.listApp);
Server.registerHandler("set-name", Entries.setName);

Server.start();

