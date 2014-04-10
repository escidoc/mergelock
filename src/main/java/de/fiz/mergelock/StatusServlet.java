package de.fiz.mergelock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		if(req.getParameter("delete") != null){
			String project = req.getParameter("delete");
			//SocketServlet.mergeLocks.remove(project);
			SocketServlet.forceUnlock(project);
		}

		PrintWriter out = resp.getWriter();

		out.write("<html>");
		out.write("<head>");
		out.write("<title>Status</title>");
		out.write("<script type=\"text/javascript\">");
		out.write("setInterval(function(){window.location.href = location.protocol+'//'+location.host+location.pathname;},2000);");
		out.write("</script>");
		out.write("</head>");
		out.write("<body>");

		out.write("<h3>Current Mergelocks: "+SocketServlet.mergeLocks.size()+"</h3>");

		for(String key : SocketServlet.mergeLocks.keySet()){
			out.write("<div>");
			out.write(SocketServlet.mergeLocks.get(key).toString());
			out.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			out.write("<a href=\"/mergelock/status?delete="+SocketServlet.mergeLocks.get(key).getProject()+"\" >Delete Mergelock</a>");
			out.write("</div>");
		}

		out.write("</body>");
		out.write("</html>");

	}

}
