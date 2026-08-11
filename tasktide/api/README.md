# TaskTide - Web API Lib
<p id="web-api">
Provides a Jakarta-RESTful API interface, with embedded Jersey Glassfish web-server, for interacting with TaskTide components. Primarily focused on TaskTide-Manager API, explictly through the ManagerCommand interface as well each of core the <strong><em>Workflow</em></strong>, <strong><em>Step</em></strong>, and <strong><em>Work Item</em></strong> services and secondary <strong><em>Job Environment</em></strong>, <strong><em>Metric Data</em></strong>, and <strong><em>Metric Profile</em></strong> services.

Library uses an AuthenticationScheme strategy to orchestrate how this is handled for different use-cases, unit-testing <strong><em>Embedded Auth Scheme</em></strong>, <strong><em>OIDC Auth Scheme</em></strong>, and <strong><em>No Auth Scheme</em></strong>. An AuthenticationFilter implements Jakartas ContainerRequestFilter hook to fetch the configured scheme, and process incoming requests according to the scheme.

The library leverages data models spec'd to standardized security practices. <strong><em>AuthPrincipal</em></strong> implements Java's Principal API as the referential class for REST resources. <strong><em>JwtSecurityContext</em></strong> implements Jakarta Security Context as a means to standardize evaluations of user principals in JWT format.

Hateoas request hook, and data point specific security contexts are templated for their incoporation into version-2. As having the RESTful API by iteself was relevant for TaskTides deployment use-case.
</p>