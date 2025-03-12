import { InputBlock, ValidationIcon } from "./LoginRegister.styles";
import { RefreshCcw, CheckCircle, XCircle } from "lucide-react";

interface NicknameVerificationProps {
  nickname: string;
  nicknameVerified: boolean | null;
  setNicknameVerified: (verified: boolean | null) => void;
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleCheckNickname: () => void;
}

export default function NicknameVerification({
  nickname,
  nicknameVerified,
  handleChange,
  handleCheckNickname,
}: NicknameVerificationProps) {
  return (
    <InputBlock isSuccess={nicknameVerified}>
      <input
        type="text"
        name="nickname"
        placeholder="사용자 닉네임"
        value={nickname}
        onChange={handleChange}
        disabled={nicknameVerified === true}
      />
      <ValidationIcon type="button" onClick={handleCheckNickname}>
        {nicknameVerified === null ? (
          <RefreshCcw />
        ) : nicknameVerified ? (
          <CheckCircle />
        ) : (
          <XCircle />
        )}
      </ValidationIcon>
    </InputBlock>
  );
}
