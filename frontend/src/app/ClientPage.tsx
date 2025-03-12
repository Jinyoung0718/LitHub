"use client";

import {
  HeroContainer,
  AnimatedShapes,
  Content,
  Title,
  Subtitle,
} from "./styles/main.styles";

export default function Page() {
  const textVariants = {
    hidden: { opacity: 0, scale: 0.9 },
    visible: {
      opacity: 1,
      scale: 1,
      transition: { duration: 1.2, ease: "easeOut" },
    },
  };

  const subtitleVariants = {
    hidden: { opacity: 0, scale: 0.9 },
    visible: {
      opacity: 1,
      scale: 1,
      transition: { duration: 1.2, ease: "easeOut" },
    },
  };

  return (
    <HeroContainer
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1.5 }}
    >
      <AnimatedShapes>
        <div className="circle large"></div>
        <div className="circle small"></div>
        <div className="rectangle"></div>
        <div className="triangle"></div>
        <div className="square"></div>
      </AnimatedShapes>

      <Content>
        <Title variants={textVariants} initial="hidden" animate="visible">
          LitHub
        </Title>
        <Subtitle
          variants={subtitleVariants}
          initial="hidden"
          animate="visible"
        >
          함께 배우고 성장하며, 독서의 가치를 재발견하세요
        </Subtitle>
      </Content>
    </HeroContainer>
  );
}
