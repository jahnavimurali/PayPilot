import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "./landing.css";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSend = async (e) => {
    e.preventDefault();
    if (!email) return;
    setLoading(true);
    try {
      await axios.post("http://localhost:9090/api/user/forgot-password", { email });
      toast.success("OTP sent to your registered phone.");
      navigate(`/reset-password?email=${encodeURIComponent(email)}`);
    } catch (err) {
      console.error(err);
      toast.error(err.response?.data?.message || err.response?.data || "Failed to send OTP");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pp-auth-wrap">
      <div className="pp-card pp-auth-card position-relative">

        <div className="pp-auth-logo">Pay<span>Pilot</span></div>
        <h2 className="pp-auth-title">Forgot password</h2>
        <p className="pp-auth-sub">Enter your email. We’ll text a 6-digit code to your registered phone.</p>

        <form onSubmit={handleSend} className="pp-form-grid">
          <div className="pp-field">
            <label htmlFor="fp-email">Email</label>
            <input
              id="fp-email"
              type="email"
              className="pp-input"
              value={email}
              onChange={(e)=>setEmail(e.target.value)}
              placeholder="you@example.com"
              required
            />
          </div>

          <button type="submit" className="pp-btn pp-btn--primary pp-btn--block" disabled={loading}>
            {loading ? "Sending…" : "Send OTP"}
          </button>
        </form>

        <div className="pp-sep"><span>or</span></div>
        <Link to="/login" className="pp-btn pp-btn--outline pp-btn--block">Back to Log In</Link>

        <div className="text-center mt-3">
            <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
        </div>

      </div>
    </div>
  );
}
