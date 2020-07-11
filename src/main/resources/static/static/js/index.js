
const main = {
    init : function () {
        const _this = this;
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
            $('#div-login').hide();
            posts.init(r.response.seq);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }

};
const posts = {
    init : function (userId) {
        const _this = this;
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
            const tableBody = $("#div-posts-list").find("tbody");
            tableBody.empty();
            const template = '<tr>\n'+'<td><img class="uk-preserve-width uk-border-circle" src="images/avatar.jpg" width="40" alt=""></td>\n'
                +'<td class="uk-table-link" id="contents"><a class="uk-link-reset" href=""></a></td>\n' +
                '<td class="uk-text-truncate" id="likes"></td>\n' +
                '<td id="comments"></td>\n' +
                '<td class="uk-text-nowrap" id="createAt"></td>\n' +
                '</tr>'
            for(let i=0; i<r.response.length; i++){
                tableBody.append(template);
                tableBody.find("tr:eq("+i+") td").each(function(){
                    const key = $(this).prop("id");
                    if(key !== undefined){
                        $(this).append(r.response[i][key]);
                    }
                });
            }

            console.log(r);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
}

main.init();