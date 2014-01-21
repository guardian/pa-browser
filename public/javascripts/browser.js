jQuery(function($){
    'use strict';

    var selectors = {
            query: ".query",
            parameters: ".parameters"
        },
        dom = (function(){
            var dom = {};
            for (var key in selectors) {
                if (selectors.hasOwnProperty(key)) {
                    dom[key] = $(selectors[key]);
                }
            }
            return dom;
        })(),
        hints = {
            teamID: {
                label: "Machester United",
                value: "1006"
            },
            playerID: {
                label: "Joe Hart",
                value: "Joe_Hart"
            }
        },
        currentFields = {},
        createReplacements = function() {
            var r = /({.*?})/g,
                query = dom.query.val(),
                fields = $.map(query.match(r), function(field){
                    return field.replace("{", "").replace("}", "");
                }).filter(function(field){
                    return field !== "apiKey";
                });
            $.each(fields, function(_, field){
                if (!currentFields.hasOwnProperty(field)) {
                    addReplacementField(field);
                }
            });
            $.each(currentFields, function(currentFieldName, _){
                if (-1 === $.inArray(currentFieldName, fields)) {
                    currentFields[currentFieldName].remove();
                    delete currentFields[currentFieldName];
                }
            });
        },
        addReplacementField = function(name) {
            var field = $("<div class='param-field'><label>" + name + ": <input type='text' name='" + name + "' /></label></div>");
            if (hints.hasOwnProperty(name)) {
                field.append($("<a href='#' class='hint' data-value='" + hints[name].value + "'>&larr; " + hints[name].label + "</a>"));
            }
            currentFields[name] = field;
            dom.parameters.append(field);
        },
        insertHint = function(hintLink) {
            $(hintLink.parent()).find("input").val(hintLink.data("value"));
        };

    dom.query.on("change", createReplacements);
    dom.query.on("keyup", createReplacements);
    dom.query.on("paste", function(){
        setTimeout(createReplacements, 10);
    });
    dom.parameters.on("click", ".hint", function(e) {
        e.preventDefault();
        insertHint($(this));
    });
});
