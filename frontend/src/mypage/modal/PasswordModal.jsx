import React, { useState, useContext } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../common/AuthContext";
import {
  Backdrop,
  Modal,
  Field,
  Input,
  ButtonRow,
  Button,
} from "./PasswordModalStyles";

const PasswordModal = ({ closeModal }) => {
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const navigate = useNavigate();
  const { logout } = useContext(AuthContext);

  const handleSubmit = async () => {
    try {
      await axios.patch("/api/user/me/password", {
        currentPassword: current,
        newPassword: next,
      });
      alert("비밀번호가 변경되었습니다.");
      closeModal();
      logout(navigate);
    } catch (err) {
      alert(err.response?.data?.message || "변경 실패");
    }
  };

  return (
    <Backdrop onClick={closeModal}>
      <Modal onClick={(e) => e.stopPropagation()}>
        <Field>
          <Input
            type="password"
            value={current}
            onChange={(e) => setCurrent(e.target.value)}
            placeholder="현재 비밀번호"
          />
        </Field>
        <Field>
          <Input
            type="password"
            value={next}
            onChange={(e) => setNext(e.target.value)}
            placeholder="새 비밀번호"
          />
        </Field>

        <ButtonRow>
          <Button onClick={closeModal}>닫기</Button>
          <Button onClick={handleSubmit}>변경</Button>
        </ButtonRow>
      </Modal>
    </Backdrop>
  );
};

export default PasswordModal;
