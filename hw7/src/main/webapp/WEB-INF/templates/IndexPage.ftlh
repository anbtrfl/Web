<#import "commons.ftlh" as c>

<@c.page>

    <ul class="articles">

        <template>
            <li>
                <article>
                    <div class="title"></div>
                    <div class="information">By <span class="author"></span>, <span class="creation-time"></span></div>
                    <div class="body"></div>
                    <div class="footer">
                        <div class="left">
                            <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                            <span class="positive-score">+173</span>
                            <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                        </div>
                        <div class="right">
                            <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                            2 days ago
                            <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                            <a href="#">68</a>
                        </div>
                    </div>
                </article>
            </li>
        </template>
        <li class="noData"></li>
    </ul>


    <script>
        $(function () {
            const $articles = $(".articles");
            const $error = $(this).find(".error");
            const $noData = $(".articles .noData");
            $noData.text("Loading...")
            <#--$.ajax({
                dataType: "json",
                data: {
                    action: "findAll"
                },
                success: function (response) {
                    response.articles.forEach(function (article) {
                        $noData.hide();
                        const $tpl = $($articles.find("template").prop("content")).clone();
                        $tpl.find(".title").text(article.title);
                        $tpl.find(".body").text(article.text);
                        $tpl.find(".author").text(article.userLogin);
                        $tpl.find(".creation-time").text(article.creationTime.toString());
                        $articles.append($tpl);
                    });
                },
                type: "POST"
            });-->
            const data = {
                action: "findAll"
            }

            ajax(data, $error, function(response) {


                response["articles"].forEach(function (article) {
                    $noData.hide();
                    const $tpl = $($articles.find("template").prop("content")).clone();
                    $tpl.find(".title").text(article.title);

                    <#--jkfjkef\nerporpo\nerer
                    <p>jjkdfj</p><p>nerjjif</p><p>sdhjsh</p> replaceAll-->

                    const str = article.text;
                    const nStr = str.replaceAll('\n', '</p><p>')
                    const nnStr = '<p>' + nStr + '</p>'


                    $tpl.find(".body").html(nnStr);
                    $tpl.find(".author").text(article.userLogin);
                    $tpl.find(".creation-time").text(article.creationTime.toString());
                    $articles.append($tpl);
                });
            });
        })
    </script>
</@c.page>
