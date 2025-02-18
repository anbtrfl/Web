<#import "commons.ftlh" as c>



<@c.page>
    <#if currentUser?has_content>
        <div class="user">
            <div class="info">
                <ul>
                    <li>id: ${currentUser.id}</li>
                    <li>login: ${currentUser.login}</li>
                    <li>creationTime: ${currentUser.creationTime}</a></li>
                </ul>
            </div>
        </div>
    <#else>
        No such user
    </#if>
</@c.page>