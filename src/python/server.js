#!/usr/bin/gjs

imports.searchPath.unshift('.');

const Soup = imports.gi.Soup;

var handlers = {};

let registerHandler = function(name, handler) {
    handlers[name] = handler;
};

let mainHandler = function(server, msg, path, query, client) {
//    print(JSON.stringify(query));
//    print(JSON.stringify(msg));
    let params = msg.request_body.data ? JSON.parse(msg.request_body.data) : {};
    msg.response_headers.set_content_type("application/json", {});
    let handlerName = path.replace("/", "");
    var res = {
        status: 401,
        data: {error: "Invalid query"}
    };
    if(handlerName in handlers) {
        res = handlers[handlerName](params, query, server, msg, path, client);
    }
    let dataStr = JSON.stringify(res.data);
    msg.status_code = res.status;
    msg.response_body.append(dataStr, dataStr.length);
    msg.response_body.complete();
};

let start = function() {
    let server = new Soup.Server({ port: 1080 });
    server.add_handler(null, mainHandler);
    server.run();
};

/*
const Server = new Lang.Class({
    Name: 'Server',

    handlers: {},

    registerHandler: function(name, handler) {
        print(this.handlers);
        print("test");
        this.handlers[name] = handler;
    },

    mainHandler: function(server, msg, path, query, client) {
        msg.response_headers.set_content_type("application/json", {});
        let handlerName = path.replace("/", "");
        if(handlerName in this.handlers) {
            let res = this.handlers[handlerName](server, msg, path, query, client);
            msg.status_code = res.status;
            let dataStr = JSON.stringify(res.data);
            msg.response_body.append(dataStr, dataStr.length);
        } else {
            msg.status_code = 401;
            let errorMessage = "Invalid query";
            msg.response_body.append(errorMessage, errorMessage.length);
        }
        msg.response_body.complete();
    },

    main: function() {
        let server = new Soup.Server({ port: 1080 });
        server.add_handler(null, this.mainHandler);
        server.run();
    }
});
*/

//let server = new Server();
//server.main();

