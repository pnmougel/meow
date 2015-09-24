const Lang = imports.lang;
const Gtk = imports.gi.Gtk;
const GdkPixbuf = imports.gi.GdkPixbuf;

Gtk.init(null, null);

let theme = Gtk.IconTheme.get_default();

let loadIcon = function(entry, entryId) {
    let icon = entry.get_icon();
    var isLoaded = false;
    if(icon) {
        if(icon.get_file) {
            let iconData = GdkPixbuf.Pixbuf.new_from_file_at_size(icon.get_file().get_path(), 64, 64);
            iconData.savev("/home/nico/.meow/cache/" + entryId + ".png", "png", [], []);
            isLoaded = true
        } else {
            if(icon.names) {
                let iconName = icon.names.toString();
                var curIcon = theme.lookup_icon(iconName, 64, 0);
                if(!curIcon) {
                    curIcon = theme.lookup_icon(iconName.toLowerCase(), 64, 0);
                }
                if(curIcon) {
                    let iconData = curIcon.load_icon();
                    iconData.savev("/home/nico/.meow/cache/" + entryId + ".png", "png", [], []);
                    isLoaded = true;
                }
            }
        }
    } else {
        isLoaded = false
    }
    return isLoaded;
};

/*
const IconLoader = new Lang.Class({
    Name: 'IconLoader',

    loadIcon: function(entry) {
        let icon = entry.get_icon();
        var isLoaded = false;
        var iconFileName = entry.get_id().replace("desktop", "png");
        if(icon) {
            if(icon.get_file) {
                let iconData = GdkPixbuf.Pixbuf.new_from_file_at_size(icon.get_file().get_path(), 64, 64);
                iconData.savev("/home/nico/.meow/cache/" + iconFileName, "png", [], []);
                isLoaded = true
            } else {
                if(icon.names) {
                    let iconName = icon.names.toString();
                    var curIcon = theme.lookup_icon(iconName, 64, 0);
                    if(!curIcon) {
                        curIcon = theme.lookup_icon(iconName.toLowerCase(), 64, 0);
                    }
                    if(curIcon) {
                        let iconData = curIcon.load_icon();
                        iconData.savev("/home/nico/.meow/cache/" + iconFileName, "png", [], []);
                        isLoaded = true;
                    }
                }
            }
        } else {
            isLoaded = false
        }
        return {
            name: iconFileName,
            isLoaded: isLoaded
        }
    }
})
*/