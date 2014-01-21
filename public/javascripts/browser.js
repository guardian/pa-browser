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
            $.each(currentFields, function(_, currentField){
                if (-1 === $.inArray(currentField, fields)) {
                    currentFields[currentField].remove();
                    delete currentFields[currentField];
                }
            });
        },
        addReplacementField = function(name) {
            var field = $("<div class='param-field'><label>" + name + ": <input type='text' name='" + name + "' /></label></div>");
            currentFields[name] = field;
            dom.parameters.append(field);
        };

    dom.query.on("change", createReplacements);
    dom.query.on("paste", function(){
        setTimeout(createReplacements, 10);
    });
});
