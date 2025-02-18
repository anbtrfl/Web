<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <th>Email</th>
            </tr>
            </thead>
            <tbody>
            <#list users as user>
            <tr>
                <td>${user.id}</td>
                <td>${user.login}</td>
                <td>${user.creationTime}</td>
                <td>${user.email}</td>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>
