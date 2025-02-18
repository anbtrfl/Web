<#-- @ftlvariable name="comment" type="ru.itmo.wp.domain.Comment"-->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="post by id">
        <#if currentPost?has_content>
            <@c.post post=currentPost/>
            <#if user??>
                <div class="form">
                    <div class="header">Write Comment ↓ </div>
                    <div class="body">
                        <form method="post" action="">
                            <input type="hidden" name="action" value="writeComment"/>
                            <div class="field">
                                <div class="name">
                                    <label for="text">Text</label>
                                </div>
                                <div class="value">
                                    <textarea id="text" name="text">${comment.text!}</textarea>
                                </div>
                                <@c.error "comment.text"/>
                            </div>
                            <div class="button-field">
                                <input type="submit" value="Comment">
                            </div>
                        </form>
                    </div>
                </div>
            </#if>
            <#list currentPost.comments as comment>
                <div class="comment">
                    <div class="information">
                        By ${comment.user.login}, ${comment.creationTime}</div>
                    <div class="body">${comment.text}</div>
                </div>
            </#list>

        <#else>
            No such post
        </#if>
    </div>
</@c.page>
