import { cookies } from "next/headers";
import { NextResponse, type NextRequest } from "next/server";
import client from "./lib/backend/apiV1/client";

export async function middleware(request: NextRequest) {
  const myCookies = await cookies();
  const accessToken = myCookies.get("accessToken");

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

function isPublicRoute(pathname: string): boolean {
  return ["/", "/login", "/signup", "/about", "/login-register"].includes(
    pathname
  );
}

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

function handleUnauthorized(): NextResponse {
  return new NextResponse("로그인이 필요합니다.", {
    status: 401,
    headers: {
      "Content-Type": "text/html; charset=utf-8",
    },
  });
}

export const config = {
  matcher: [
    "/((?!api/auth|api/public|favicon.ico|_next/static|_next/image|.*\\.png$|.*\\.jpg$|.*\\.jpeg$|.*\\.svg$|.*\\.css$|.*\\.js$).*)",
  ],
};
