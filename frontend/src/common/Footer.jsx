import styled from "styled-components";
import { FaGithub, FaEnvelope } from "react-icons/fa";

const FooterContainer = styled.footer`
  background: #fff8dc;
  color: #333;
  padding: 2rem 1rem;
  text-align: center;
  font-family: "Poppins", sans-serif;
  font-size: 0.9rem;

  @media (max-width: 768px) {
    padding: 1.5rem 1rem;
  }
`;

const IconSection = styled.div`
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 1rem;

  a {
    color: #555;
    font-size: 1.4rem;
    transition: color 0.3s ease, transform 0.2s ease;

    &:hover {
      color: #f39c12;
      transform: scale(1.1);
    }
  }
`;

const BottomSection = styled.div`
  font-size: 0.8rem;
  color: #777;
  margin-top: 1rem;

  @media (max-width: 768px) {
    font-size: 0.75rem;
  }
`;

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
      <BottomSection>© 2025 LitHub. All rights reserved.</BottomSection>
    </FooterContainer>
  );
}

export default Footer;
