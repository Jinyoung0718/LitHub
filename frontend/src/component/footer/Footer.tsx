"use client";

import { FaGithub, FaEnvelope } from "react-icons/fa";
import { FooterContainer, IconSection, BottomSection } from "./Footer.styles";

function Footer() {
  return (
    <FooterContainer>
      <IconSection>
        <a
          href="https://github.com/Jinyoung0718"
          target="_blank"
          rel="noopener noreferrer"
          aria-label="GitHub"
        >
          <FaGithub />
        </a>
        <a href="mailto:sojinyeong891@gmail.com" aria-label="Email">
          <FaEnvelope />
        </a>
      </IconSection>
      <BottomSection>
        © 2024 MileStone Music. All rights reserved.
      </BottomSection>
    </FooterContainer>
  );
}

export default Footer;
