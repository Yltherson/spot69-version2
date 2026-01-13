<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
%>
<header class="top-bar">
        <div class="d-flex align-items-center">
            <div class="top-icon" onclick="goBack()"><i class="fas fa-chevron-left"></i> Back</div>
            <div class="top-icon" onclick="showSearch()"><i class="fas fa-search"></i> SEARCH</div>
            <div class="top-icon" onclick="showPLU()"><i class="fas fa-th"></i> PLU</div>
        </div>
        
        <div class="sold"><i class="mr-10">124 000 HTG</i></div>
        <div class="brand-logo">
        	<!-- <i class="fas fa-fire"></i> -->
        	<img alt="" src="./image/logo.jpg" style="width: 100px;">
        </div>
        <div class="sold"><i class="ml-10"></i>1 060 US</div>
<!-- <i class="fas fa-utensils"></i> -->
        <div class="d-flex align-items-center">
            <!-- <div class="top-icon"><i class="fas fa-beer"></i> SEND-BAR</div> -->
            <div class="top-icon mt-4"> SETTINGS</div>
            <div class="top-icon">
                <a class="nav-link" href="UtilisateurServlet?action=lister" style="color: white; text-decoration: none;">
                    <!-- <i class="fe fe-users"></i> -->
                    <i class="fas fa-receipt"></i>
                    <span class="ml-3 item-text">CONFIG</span>
                </a>
            </div>
            <!-- <div class="top-icon"><i class="fas fa-receipt"></i> CONFIG</div> -->
            <!-- <div class="top-icon">
                <a class="nav-link" href="RoleServlet?action=lister">
                    <i class="fas fa-user-plus"></i>
                    <span class="ml-3 item-text">USERS</span>
                </a>
            </div> -->
            <div class="top-icon">
                <a class="nav-link" href="RoleServlet?action=lister" style="color: white; text-decoration: none;">
                    <!-- <i class="fe fe-users"></i> -->
                    <i class="fas fa-user-plus"></i>
                    <span class="ml-3 item-text">USERS</span>
                </a>
            </div>
            <div class="top-icon">
                <ul class="nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-muted pr-0 d-flex align-items-center gap-2" href="#" id="navbarDropdownMenuLink" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <div class="user-profile">
                                <img src="https://i.pravatar.cc/100?img=11" alt="User" class="avatar-img">
                                <div class="text-xs font-weight-bold text-uppercase">
                                    <%
                                        if (username != null && !username.isEmpty()) {
                                    %>
                                        <%= username %>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </a>
                        
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownMenuLink">
                            <a class="dropdown-item d-flex align-items-center" href="#">
                                <i class="fe fe-user fe-16 mr-2"></i> Profile
                            </a>
                            <a class="dropdown-item d-flex align-items-center" href="UtilisateurServlet?action=logout&who=ADM">
                                <i class="fe fe-log-out fe-16 mr-2"></i> Déconnexion
                            </a>
                            <a class="dropdown-item d-flex align-items-center" href="<%=request.getContextPath()%>/index.jsp">
                                <i class="fe fe-home fe-16 mr-2"></i> Acceder au site
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </header>