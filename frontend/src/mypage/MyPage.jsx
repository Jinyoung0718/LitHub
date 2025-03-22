import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { AuthContext } from "../common/AuthContext";
import { Container } from "./MyPageStyles";
import ProfileCard from "./ProfileCard";
import StreakSummary from "./StreakSummary";
import ReadingHeatmap from "./ReadingHeatmap";
import MonthlyStatsChart from "./MonthlyStatsChart";

const MyPage = () => {
  const { accessToken, isLoading } = useContext(AuthContext);
  const [myPageData, setMyPageData] = useState(null);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [availableYears, setAvailableYears] = useState([]);

  useEffect(() => {
    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 5 }, (_, i) => currentYear - i);
    setAvailableYears(years);
  }, []);

  useEffect(() => {
    const fetchMyPageData = async () => {
      try {
        const response = await axios.get(`/api/user/me?year=${selectedYear}`);
        setMyPageData(response.data.result);
      } catch (error) {
        console.error("마이페이지 데이터 가져오기 실패:", error);
      }
    };

    if (!isLoading && accessToken) {
      fetchMyPageData();
    }
  }, [accessToken, isLoading, selectedYear]);

  if (isLoading) return <div>로딩 중...</div>;
  if (!accessToken) return <div>로그인이 필요합니다.</div>;
  if (!myPageData) return <div>데이터를 불러오는 중입니다...</div>;

  const { userProfile, readingStats } = myPageData;

  return (
    <Container>
      <div style={{ marginBottom: "20px" }}>
        <label htmlFor="year-select">연도 선택: </label>
        <select
          id="year-select"
          value={selectedYear}
          onChange={(e) => setSelectedYear(parseInt(e.target.value))}
        >
          {availableYears.map((year) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
      </div>

      <ProfileCard userProfile={userProfile} />
      <StreakSummary streak={readingStats.readingStreak} />
      <ReadingHeatmap
        readingRecords={readingStats.readingRecords}
        selectedYear={selectedYear}
      />
      <MonthlyStatsChart monthlyStats={readingStats.monthlyStats} />
    </Container>
  );
};

export default MyPage;
