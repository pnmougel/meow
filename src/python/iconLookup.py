#!/usr/bin/env python3

import sys
from gi.repository import Gtk

icon_name = sys.argv[1]
save_to = sys.argv[2]

if icon_name:
    theme = Gtk.IconTheme.get_default()
    for res in range(64, 0, -2):
        icon = theme.lookup_icon(icon_name, res, 0)
        if icon:
            icon_data = icon.load_icon()
            icon_data.savev(save_to, "png", [], [])
            print("ok")
            exit(0)

    print("nok") 
