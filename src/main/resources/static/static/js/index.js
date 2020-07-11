
var main = {
    init : function () {
        var _this = this;
        if(localStorage.getItem('apiToken') !== undefined){
            _this.me();
        } else {
            $('#div-login').show();
            $('#btn-auth').on('click', function () {
                _this.auth();
            });
        }
    },
    auth : function () {
        var data = {
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
            alert('로그인 되었습니다.');
            localStorage.setItem('apiToken', r.response.apiToken);
            main.me();
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    me :function () {
        $.ajax({
            type: 'GET',
            url: '/api/user/me',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            // Fetch the stored token from localStorage and set in the header
            headers: {'api_key': 'Bearer ' + localStorage.getItem('apiToken')}
        }).done(function(r) {
            posts.init(r.response.seq);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }

};
var posts = {
    init : function (userId) {
        var _this = this;
        _this.list(userId);
    },
    list :function (userId) {
        $.ajax({
            type: 'GET',
            url: '/api/user/' + userId + '/post/list',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            headers: {'api_key': 'Bearer ' + localStorage.getItem('apiToken')}
        }).done(function(r) {
            const gridData = r.response;

            const grid = new tui.Grid({
                el: document.getElementById('grid'),
                data: gridData,
                scrollX: false,
                scrollY: false,
                columns: [
                    {
                        header: '내용',
                        name: 'contents'
                    },
                    {
                        header: '좋아요',
                        name: 'likes'
                    },
                    {
                        header: '댓글',
                        name: 'comments'
                    },

                    {
                        header: '작성일시',
                        name: 'createAt'
                    }
                ]
            });
            $('#div-posts-list').show();
            console.log(r);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
}

main.init();