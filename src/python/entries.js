#!/usr/bin/gjs

imports.searchPath.unshift('.');

const Gtk = imports.gi.Gtk;
const Gio = imports.gi.Gio;
const GLib = imports.gi.GLib;
const Sha = imports.sha;

const iconloader = imports.iconloader;
//var keyfile = new GLib.KeyFile()
//keyfile.load_from_file("/home/nico/.local/share/applications/facebook.desktop", GLib.KeyFileFlags.KEEP_COMMENTS | GLib.KeyFileFlags.KEEP_TRANSLATIONS);
//print(keyfile.get_groups());
//print(keyfile.get_string("Desktop Entry", "Name"));
//keyfile.set_string("Desktop Entry", "Name", "Facebook 2");
//print(keyfile.get_string("Desktop Entry", "Name"));
//keyfile.save_to_file("/home/nico/.local/share/applications/facebook2.desktop");

Gtk.init(null, null);

//
//let authority = Polkit.Authority.get();
//let subject = Polkit.UnixProcess.new(os.getppid());
//let mainLoop = function() {
//    print("Run as root ?")
//};
//authority.check_authorization(subject,
//    "org.freedesktop.policykit.exec",
//    null,
//    Polkit.CheckAuthorizationFlags.ALLOW_USER_INTERACTION,
//    cancellable,
//    check_authorization_cb,
//    mainloop)

//let details = new Polkit.Details()

var entries = {};

let listApp = function(params, query, server, msg, path, client) {
    entries = {};

    let data = Gio.AppInfo.get_all().map(function(e) {
        let entryId = "e" + Sha.hex_sha1(e.get_filename());
        entries[entryId] = e;

        let cateories = e.get_categories() ? e.get_categories().split(";") : [];
        return {
            id: entryId,
            name: e.get_name(),
            shouldShow: e.should_show(),
            commandLine: e.get_commandline(),
            description: e.get_description(),
            displayName: e.get_display_name(),
            executable: e.get_executable(),
            actions: e.list_actions(),
            keywords: e.get_keywords(),
            categories: cateories,
            fileName: e.get_filename(),
            isHidden: e.get_is_hidden(),
            genericName: e.get_generic_name(),
            noDisplay: e.get_nodisplay(),
            iconLoaded: iconloader.loadIcon(e, entryId)
        };
    });
    return {
        data: data,
        status: 200
    }
};

let setCategories = function(params, query, server, msg, path, client) {

}

let setName = function(params, query, server, msg, path, client) {
    if("id" in params && "name" in params) {
        updateEntry(params.id, function(keyFile) {
            keyFile.set_string("Desktop Entry", "Name", params.name);
        });
    }
    return {
        data: {message: "Ok"},
        status: 200
    }
};

let updateEntry = function(entryId, f) {
    if(entries.entryId) {
        let entry = entries[entryId];
        let path = entry.get_filename();
        var keyfile = new GLib.KeyFile();
        keyfile.load_from_file(path, GLib.KeyFileFlags.KEEP_COMMENTS | GLib.KeyFileFlags.KEEP_TRANSLATIONS);
        f(keyfile);
        keyfile.save_to_file(path);
    }
};