import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const MonthlyStatsChart = ({ monthlyStats }) => {
  const data = monthlyStats.map((stat) => ({
    name: `${stat.month}월`,
    time: stat.totalReadingTime,
  }));

  const isEmpty = data.length === 0;

  return (
    <div style={{ marginTop: "40px" }}>
      {isEmpty ? (
        <p style={{ color: "#777", fontStyle: "italic" }}>
          아직 독서 기록이 없습니다.
        </p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Line
              type="monotone"
              dataKey="time"
              stroke="#ffa000"
              strokeWidth={3}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
};

export default MonthlyStatsChart;
