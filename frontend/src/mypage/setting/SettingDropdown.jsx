import React, { useContext, useState } from "react";
import { AuthContext } from "../../common/AuthContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import PasswordModal from "../modal/PasswordModal";
import { FaCog } from "react-icons/fa";
import { Wrapper, ToggleButton, Menu, Item } from "./SettingDropdownStyles";

const SettingDropdown = ({ onUpdate }) => {
  const [show, setShow] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleNicknameUpdate = async () => {
    const newNick = prompt("새 닉네임을 입력해주세요 (3~50자)");
    if (!newNick) return;

    try {
      await axios.patch("/api/user/nickname", { nickName: newNick });
      alert("닉네임이 변경되었습니다.");
      if (onUpdate) onUpdate(new Date().getFullYear());

      setShow(false);
    } catch (err) {
      alert(err.response?.data?.message || "닉네임 변경 실패");
    }
  };

  const handlePasswordClick = () => {
    setShow(false);
    setShowPasswordModal(true);
  };

  const handleDeleteUser = async () => {
    if (!window.confirm("정말 계정을 삭제하시겠습니까?")) return;

    try {
      await axios.post("/api/user/delete-user");
      alert("계정이 삭제되었습니다.");
      logout(navigate);
    } catch (err) {
      alert(err.response?.data?.message || "삭제 실패");
    }
  };

  return (
    <Wrapper>
      <ToggleButton onClick={() => setShow(!show)}>
        <FaCog style={{ marginRight: "6px" }} />
      </ToggleButton>
      {show && (
        <Menu>
          <Item onClick={handleNicknameUpdate}>닉네임 수정</Item>
          <Item onClick={handlePasswordClick}>비밀번호 수정</Item>
          <Item onClick={handleDeleteUser}>계정 삭제</Item>
        </Menu>
      )}
      {showPasswordModal && (
        <PasswordModal closeModal={() => setShowPasswordModal(false)} />
      )}
    </Wrapper>
  );
};

export default SettingDropdown;
