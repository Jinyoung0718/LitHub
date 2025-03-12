import { cookies } from "next/headers";
import { NextResponse, type NextRequest } from "next/server";
import client from "./lib/backend/apiV1/client";

export async function middleware(request: NextRequest) {
  const myCookies = await cookies();
  const accessToken = myCookies.get("accessToken");

  // ✅ 인증이 필요 없는 경로는 미들웨어 실행 제외
  if (isPublicRoute(request.nextUrl.pathname)) {
    return NextResponse.next();
  }

  if (!accessToken) {
    return handleUnauthorized();
  }

  const isAuthenticated = await checkAuth(request);

  if (!isAuthenticated) {
    const isAuthenticatedAfterRefresh = await checkAuth(request);
    if (!isAuthenticatedAfterRefresh) {
      return handleUnauthorized();
    }
  }

  return NextResponse.next();
}

// ✅ 로그인 없이 접근 가능한 페이지 정의
function isPublicRoute(pathname: string): boolean {
  return ["/", "/login", "/signup", "/about", "/login-register"].includes(
    pathname
  );
}

// ✅ 로그인 상태 확인 (백엔드 `/api/user/check` 호출)
async function checkAuth(request: NextRequest): Promise<boolean> {
  try {
    const response = await client.GET("/api/user/check", {
      headers: {
        cookie: request.headers.get("cookie") || "",
      },
    });

    return response.response.status === 200;
  } catch (error) {
    console.error("로그인 상태 확인 실패:", error);
    return false;
  }
}

// ✅ 미인증 상태 처리
function handleUnauthorized(): NextResponse {
  return new NextResponse("로그인이 필요합니다.", {
    status: 401,
    headers: {
      "Content-Type": "text/html; charset=utf-8",
    },
  });
}

// ✅ matcher 수정: 로그인 필요 없는 페이지 제외
export const config = {
  matcher: [
    "/((?!api/auth|api/public|favicon.ico|_next/static|_next/image|.*\\.png$|.*\\.jpg$|.*\\.jpeg$|.*\\.svg$|.*\\.css$|.*\\.js$).*)",
  ],
};
