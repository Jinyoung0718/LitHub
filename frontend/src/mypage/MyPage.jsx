import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { AuthContext } from "../common/AuthContext";
import { Container } from "./MyPageStyles";
import ProfileCard from "./profile/ProfileCard";
import ReadingHeatmap from "./heatmap/ReadingHeatmap";
import MonthlyStatsChart from "./MonthlyStatsChart";
import SettingDropdown from "./setting/SettingDropdown";
import ReadingTimerModal from "./timer/ReadingTimerModal";
import {
  StartReadingWrapper,
  StartReadingButton,
  DropdownWrapper,
  DropdownLabel,
  DropdownSelect,
} from "./MyPageStyles";

const MyPage = () => {
  const { accessToken, isLoading } = useContext(AuthContext);
  const [showTimerModal, setShowTimerModal] = useState(false);
  const [myPageData, setMyPageData] = useState(null);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [availableYears, setAvailableYears] = useState([]);

  useEffect(() => {
    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 5 }, (_, i) => currentYear - i);
    setAvailableYears(years);
  }, []);

  const fetchMyPageData = async (year) => {
    try {
      const response = await axios.get(`/api/user/me?year=${year}`, {
        headers: {
          "Cache-Control": "no-cache",
          Pragma: "no-cache",
          Expires: "0",
        },
      });
      setMyPageData(response.data.result);
    } catch (error) {
      console.error("마이페이지 데이터 가져오기 실패:", error);
    }
  };

  const handleAddReadingTime = async (minutes) => {
    try {
      await axios.post(`/api/user/reading-log?minutes=${minutes}`);
      await fetchMyPageData(selectedYear);
    } catch (error) {
      console.error("읽기 기록 추가 실패:", error);
    }
  };

  useEffect(() => {
    if (!isLoading && accessToken) {
      fetchMyPageData(selectedYear);
    }
  }, [accessToken, isLoading, selectedYear]);

  if (isLoading) return <div>로딩 중...</div>;
  if (!accessToken) return <div>로그인이 필요합니다.</div>;
  if (!myPageData) return <div>데이터를 불러오는 중입니다...</div>;

  const { userProfile, readingStats } = myPageData;

  return (
    <Container>
      <SettingDropdown onUpdate={fetchMyPageData} />
      <ProfileCard
        userProfile={userProfile}
        readingStreak={readingStats.readingStreak}
      />

      <StartReadingWrapper>
        <StartReadingButton onClick={() => setShowTimerModal(true)}>
          읽기 시작
        </StartReadingButton>
      </StartReadingWrapper>

      {showTimerModal && (
        <ReadingTimerModal
          onClose={() => setShowTimerModal(false)}
          onSave={handleAddReadingTime}
        />
      )}

      <DropdownWrapper>
        <DropdownLabel htmlFor="year-select">연도 선택</DropdownLabel>
        <DropdownSelect
          id="year-select"
          value={selectedYear}
          onChange={(e) => setSelectedYear(parseInt(e.target.value))}
        >
          {availableYears.map((year) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </DropdownSelect>
      </DropdownWrapper>

      <ReadingHeatmap
        readingRecords={readingStats.readingRecords}
        selectedYear={selectedYear}
      />

      <MonthlyStatsChart monthlyStats={readingStats.monthlyStats} />
    </Container>
  );
};

export default MyPage;
