
const main = {
    init : function () {
        const _this = this;
        if(localStorage.getItem('apiToken') === undefined || localStorage.getItem('apiToken') === null){
            $('#div-login').show();
            $('#btn-auth').on('click', function () {
                _this.auth();
            });
        } else {
            _this.me(localStorage.getItem('apiToken'));
            $("#btn-logout").click(function (){
                localStorage.removeItem("apiToken");
                main.init();
            });

        }

    },
    auth : function () {
        const data = {
            principal: $('#email').val(),
            credentials: $('#password').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/auth',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(r) {
            localStorage.setItem('apiToken', r.response.apiToken);
            main.me(r.response.apiToken);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    me :function (apiToken) {
        $.ajax({
            type: 'GET',
            url: '/api/user/me',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            // Fetch the stored token from localStorage and set in the header
            headers: {'api_key': 'Bearer ' + apiToken}
        }).done(function(r) {
            $('#div-login').hide();
            post.init(r.response.seq, apiToken);
        }).fail(function (error) {
           alert(JSON.stringify(error));
            localStorage.removeItem("apiToken");
            main.init();
        });
    }
};

const post = {
    init : function(userId, apiToken){
        const _this = this;
        $(".div-navbar").show();
        post.list(userId, apiToken);
        $(document).on('click','.btn-like', function (){
            const postId = $(this).parents(".div-post").attr("seq");
            _this.like(userId, postId, apiToken, $(this));
        });
        $("#btn-new-post").click(function () {
            _this.write(userId, apiToken);
            _this.list(userId, apiToken);
        });

    },
    write :function (userId, apiToken) {
        const newpost = {
            contents : $("#posting-contents").val()
        }
        $.ajax({
            type: 'POST',
            url: '/api/post',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data : JSON.stringify(newpost),
            headers: {'api_key': 'Bearer ' + apiToken}
        }).done(function(r) {
            UIkit.modal('#modal-new-post').hide();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    list : function(userId, apiToken, offset, limit){
        if(offset == null && limit == null){
            offset = 0;
            limit = 5;
        }
        $("#post-list").empty();
        const scrollspy = $('#scrollspy');
        const spinner = $('#scrollspy .spinner');
        const addContent = function (offset) {
            spinner.removeClass('uk-hidden');
                //add to dom
                post.getData(userId, apiToken, offset, limit);
                spinner.addClass('uk-hidden');
        };
        //add content when inview triggers, 200px before end of list comes into view
        UIkit.util.on(scrollspy, 'inview', function() {
            offset = offset + limit;
            addContent(offset);
        });
        //initial content
        addContent(offset);

    },
    getData :function (userId, apiToken, offset, limit) {
        $.ajax({
            type: 'GET',
            url: '/api/user/' + userId + '/post/list?offset=' + offset + '&limit=' + limit,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            headers: {'api_key': 'Bearer ' + apiToken}
        }).done(function(r) {
            const cardContainer = $("#post-list");
            const template = $('script[type="text/postTemplate"]').html();
            for(let i=0; i<r.response.length; i++){
                cardContainer.append(template);
                cardContainer.find(".div-post:last").attr("seq", r.response[i]["seq"]);
                cardContainer.find(".div-post:last .post-attribute").each(function(){
                    const key = $(this).attr("name");
                    if(key !== undefined){
                        if(key === "writer"){
                            $(this).append(r.response[i]["writer"]["name"]);
                        } else {
                            $(this).append(r.response[i][key]);
                        }
                    }
                });
            }
            $('#post-list').show();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    like :function (userId, postId, apiToken, el) {
        $.ajax({
            type: 'PATCH',
            url: '/api/user/' + userId + '/post/' + postId +'/like',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            headers: {'api_key': 'Bearer ' + apiToken}
        }).done(function(r) {
            el.next().empty().append(r.response.likes)
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};
main.init();