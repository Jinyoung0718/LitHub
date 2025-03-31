import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import {
  Nickname,
  ProfileContainer,
  ProfileHeader,
  ProfileAvatarWrapper,
  ProfileAvatar,
  DropdownWrapper,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  InfoList,
  InfoItem,
  Label,
  Value,
  PointBarContainer,
  ProgressBar,
  ProgressFill,
  ProgressTextInBar,
  TierBadge,
  NicknameRow,
} from "./ProfileCardStyles";
import { FaEllipsisV, FaUpload, FaUndo } from "react-icons/fa";

const getTierProgress = (point, tierMin, tierMax) => {
  const range = tierMax - tierMin;
  const progress = point - tierMin;
  return Math.min(100, Math.floor((progress / range) * 100));
};

const ProfileCard = ({ userProfile, readingStreak }) => {
  const fileInputRef = useRef();
  const [profileImageUrl, setProfileImageUrl] = useState(
    userProfile.profileImageUrlLarge
  );
  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    setProfileImageUrl(`${userProfile.profileImageUrlLarge}?t=${Date.now()}`);
  }, [userProfile.profileImageUrlLarge]);

  const handleUploadClick = () => {
    fileInputRef.current.click();
    setShowDropdown(false);
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await axios.post("/api/user/profile/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setProfileImageUrl(`${res.data.result}?t=${Date.now()}`);
    } catch (err) {
      console.error("업로드 실패", err);
    }
  };

  const handleDelete = async () => {
    try {
      const res = await axios.delete("/api/user/profile/delete");
      setProfileImageUrl(`${res.data.result}?t=${Date.now()}`);
    } catch (err) {
      console.error("삭제 실패", err);
    }
    setShowDropdown(false);
  };

  let max = 0;
  let percent = 0;

  if (userProfile.tier === "PLATINUM") {
    max = userProfile.point;
    percent = 100;
  } else {
    const tierRange = {
      BRONZE: [0, 1000],
      SILVER: [1000, 5000],
      GOLD: [5000, 10000],
    };
    const [min, maxValue] = tierRange[userProfile.tier] || [0, 1000];
    max = maxValue;
    percent = getTierProgress(userProfile.point, min, maxValue);
  }

  return (
    <ProfileContainer>
      <ProfileHeader>
        <ProfileAvatarWrapper>
          <ProfileAvatar
            src={profileImageUrl}
            alt="프로필"
            className={userProfile.tier.toLowerCase()}
          />
          <DropdownWrapper>
            <DropdownToggle onClick={() => setShowDropdown(!showDropdown)}>
              <FaEllipsisV />
            </DropdownToggle>
            {showDropdown && (
              <DropdownMenu>
                <DropdownItem onClick={handleUploadClick}>
                  <FaUpload style={{ marginRight: "5px" }} />
                  프로필 업로드
                </DropdownItem>
                <DropdownItem onClick={handleDelete}>
                  <FaUndo style={{ marginRight: "5px" }} />
                  기본 프로필
                </DropdownItem>
              </DropdownMenu>
            )}
          </DropdownWrapper>
        </ProfileAvatarWrapper>

        <NicknameRow>
          <Nickname>{userProfile.nickname}</Nickname>
          <TierBadge className={userProfile.tier.toLowerCase()}>
            {userProfile.tier}
          </TierBadge>
        </NicknameRow>

        <input
          type="file"
          accept="image/*"
          ref={fileInputRef}
          style={{ display: "none" }}
          onChange={handleFileChange}
        />
      </ProfileHeader>

      <InfoList>
        <InfoItem>
          <Label>연속 독서</Label>
          <Value>{readingStreak}일</Value>
        </InfoItem>
        <PointBarContainer>
          <ProgressBar>
            <ProgressFill style={{ width: `${percent}%` }} />
            <ProgressTextInBar>
              {userProfile.point} / {max}점 ({percent}%)
            </ProgressTextInBar>
          </ProgressBar>
        </PointBarContainer>
      </InfoList>
    </ProfileContainer>
  );
};

export default ProfileCard;
