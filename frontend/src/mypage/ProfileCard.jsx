import React from "react";
import {
  ProfileSection,
  ProfileImage,
  InfoBox,
  Label,
  Value,
} from "./MyPageStyles";

const ProfileCard = ({ userProfile }) => (
  <ProfileSection>
    <ProfileImage src={userProfile.profileImageUrlLarge} alt="프로필" />
    <InfoBox>
      <Label>닉네임</Label>
      <Value>{userProfile.nickname}</Value>
      <Label>이메일</Label>
      <Value>{userProfile.email}</Value>
      <Label>티어</Label>
      <Value>{userProfile.tier}</Value>
      <Label>포인트</Label>
      <Value>{userProfile.point}</Value>
    </InfoBox>
  </ProfileSection>
);

export default ProfileCard;
