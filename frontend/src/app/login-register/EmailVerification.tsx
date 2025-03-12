import { useState } from "react";
import { EmailInputGroup } from "./LoginRegister.styles";
import { RefreshCcw, CheckCircle, XCircle } from "lucide-react";
import { useAuth } from "../hooks/useAuth";

export default function EmailVerification({
  email,
  setFormData,
  verificationCode,
}: any) {
  const { sendEmailCode, verifyEmailCode } = useAuth();
  const [emailVerified, setEmailVerified] = useState<boolean | null>(null);
  const [isRequestLocked, setIsRequestLocked] = useState(false);

  const handleEmailVerification = async () => {
    if (isRequestLocked) return;
    setIsRequestLocked(true);
    await sendEmailCode(email);
    setTimeout(() => setIsRequestLocked(false), 30000);
  };

  const handleVerifyEmailCode = async () => {
    const isValid = await verifyEmailCode(email, verificationCode);
    setEmailVerified(isValid);
  };

  return (
    <>
      <EmailInputGroup isSuccess={emailVerified}>
        <input
          type="email"
          name="email"
          placeholder="사용자 이메일"
          value={email}
          onChange={(e) =>
            setFormData((prev: any) => ({ ...prev, email: e.target.value }))
          }
          disabled={emailVerified === true}
        />
        <button
          type="button"
          onClick={handleEmailVerification}
          disabled={isRequestLocked}
        >
          <RefreshCcw />
        </button>
      </EmailInputGroup>

      <EmailInputGroup isSuccess={emailVerified}>
        <input
          type="text"
          name="verificationCode"
          placeholder="이메일 인증번호"
          value={verificationCode}
          onChange={(e) =>
            setFormData((prev: any) => ({
              ...prev,
              verificationCode: e.target.value,
            }))
          }
        />
        <button type="button" onClick={handleVerifyEmailCode}>
          {emailVerified === null ? (
            <RefreshCcw />
          ) : emailVerified ? (
            <CheckCircle />
          ) : (
            <XCircle />
          )}
        </button>
      </EmailInputGroup>
    </>
  );
}
