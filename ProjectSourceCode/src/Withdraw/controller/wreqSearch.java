package Withdraw.controller;

import java.io.IOException;
import java.sql.SQLException;

import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Withdraw.model.WithdrawVO;
import service.withdraw.withdrawService;



/**
 * Servlet implementation class wreqList
 */
@WebServlet("/wreqSearch.do")
public class wreqSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public wreqSearch() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		execute(request, response);
	}

	protected void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String wsearchbar = request.getParameter("wsearchbar");
		System.out.println("wsearchbar : " + wsearchbar);
		String wtype = request.getParameter("wtype");
		System.out.println("wtype:" + wtype);

		withdrawService wsv = new withdrawService();

		// 페이징
		int pageSize = 10; // 페이지 당 글 수
		int pageBlock = 10; // 페이지 링크 수
		try {
			// 총 글 개수
			int nCount = wsv.wreqSearchCount(wtype, wsearchbar);
			System.out.println("nCount : " + nCount);

			// 페이지 수 초기화
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			} else if (pageNum.equals("")) {
				pageNum = "1";
			}
			// startPage , endPage 구하는 식
			int currentPage = 1;
			try {
				currentPage = Integer.parseInt(pageNum);
			} catch (Exception e) {
				e.printStackTrace();

			}

			int pageCount = (nCount / pageSize) + (nCount % pageSize == 0 ? 0 : 1);
			int startPage = 1;
			int endPage = 1;
			if (currentPage % pageBlock == 0) {
				startPage = ((currentPage / pageBlock) - 1) * pageBlock + 1;
			} else {
				startPage = ((currentPage / pageBlock)) * pageBlock + 1;
			}
			endPage = startPage + pageBlock - 1;
			if (endPage > pageCount)
				endPage = pageCount;

			// 페이징 rownum 구하기
			int start = ((currentPage - 1) * pageSize) + 1; // 거의 공식
			int end = start + pageSize - 1; // currentPage*pageSize
			System.out.println(start + " - " + end);

			// 이전 다음 기능
			int prev = 1;
			int next = 1;
			next = endPage + 1;
			prev = startPage - 1;
			if (startPage != 1) {
				prev = startPage - 1;
			}
			if (endPage > pageCount) {
				next = endPage + 1;
			}
			System.out.println(prev + "이전 - 다음" + next);

			// 보내주
			if (wsearchbar == null || wsearchbar == "") {
				request.setAttribute("search_error", "검색어를 입력해주세요");
				System.out.println("검색어가 null 인 경우");
				RequestDispatcher disp = request.getRequestDispatcher("./wreqList.do");
				disp.forward(request, response);
			} else {
				try {
					List<WithdrawVO> wdlist = wsv.wreqSearch(wtype, wsearchbar, start, end);
					if (wdlist != null) {
						request.setAttribute("startPage", startPage);
						request.setAttribute("endPage", endPage);
						request.setAttribute("PageNum", currentPage);
						request.setAttribute("pageCount", pageCount);
						request.setAttribute("prev", prev);
						request.setAttribute("next", next);
						request.setAttribute("wtype", wtype);
						request.setAttribute("wdlist", wdlist); /* 변경 : el태그 - jsp이랑 맞추기 */
						request.setAttribute("wsearchbar", wsearchbar);
						System.out.println(wdlist.size() + ", " + startPage + ", " + endPage);
						RequestDispatcher disp1 = request.getRequestDispatcher("./manage/wManageSearch.jsp"); /* 변경 : 경로 */
						disp1.forward(request, response);
					} else {
						// null elert
					}
				} catch (SQLException e) {
					e.printStackTrace();
					request.setAttribute("errorMsg", "존재하지 않는 회원입니다. 다시 입력해주세요 :(");
					RequestDispatcher disp1 = request.getRequestDispatcher("./manage/wManage1.jsp"); /* 변경 : 경로 */
					disp1.forward(request, response);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}

	}
}
