import client from "@/lib/backend/apiV1/client";

export const useAuth = () => {
  const sendEmailCode = async (email: string) => {
    await client.POST("/api/auth/send-email-code", {
      body: { email },
    });
  };

  const verifyEmailCode = async (
    email: string,
    code: string
  ): Promise<boolean> => {
    try {
      const response = await client.POST("/api/auth/verify-email", {
        body: JSON.stringify({ email, code }),
        headers: { "Content-Type": "application/json" },
      }); // 다 DTO 클래스로 만들기기

      console.log(response);
      return response.response.status === 200;
    } catch (error) {
      console.error(error);
      return false;
    }
  };

  const checkNickname = async (nickname: string): Promise<boolean> => {
    const response = await client.POST("/api/auth/check-nickname", {
      body: { nickName: nickname },
    });
    return response.response.status === 200;
  };

  const signup = async (nickname: string, email: string, password: string) => {
    await client.POST("/api/auth/signup", {
      body: { nickName: nickname, userEmail: email, userPassword: password },
    });
  };

  const login = async (email: string, password: string) => {
    await client.POST("/api/auth/basic/login" as any, {
      body: { username: email, password: password },
    });
  };

  return { sendEmailCode, verifyEmailCode, checkNickname, signup, login };
};
