// import React, { useState } from "react";
// import axios from "axios";
// import { useNavigate, Link } from "react-router-dom";
// import { toast } from "react-toastify";
// import "../styles/auth.css";   
// import "./landing.css";        

// const Signup = () => {
//   const navigate = useNavigate();
//   const [formData, setFormData] = useState({ 
//     name: "", 
//     email: "", 
//     password: "",
//     panCardNumber:"",
//     bankAccountNumber:"",
//     ifscCode:"",
//     bankingPartner:""
//   });
//   const [loading, setLoading] = useState(false);
//   const [passwordMismatch,setPasswordMismatch]=useState(null);

//   const handleChange = (e) => {
//     setFormData({ ...formData, [e.target.name]: e.target.value });
//   };

//   const handleConfirmChange = (e) => {
//     if(formData.password===""){
//       setPasswordMismatch(null);
//     }
//      else if(e.target.value!==formData.password){
//       setPasswordMismatch(false);
//     } else {
//       setPasswordMismatch(true);
//     }
//   }

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     if(passwordMismatch===false || passwordMismatch===null){
//       toast.error("Password Does not match");
//       return;
//     }
//     if (loading) return;
//     setLoading(true);
//     try {
//       await axios.post("http://localhost:9090/api/user/signup", formData);
//       toast.success("Signup successful! Redirecting to login…");
//       setTimeout(() => navigate("/login"), 1200);
//     } catch (err) {
//       console.error("Signup failed:", err);
//       toast.error("Signup failed: " + (err.response?.data?.message || err.response?.data || err.message));
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="pp-auth-wrap">
//       <div className="pp-card pp-auth-card position-relative">
//         <div className="pp-auth-logo">Pay<span>Pilot</span></div>
//         <h2 className="pp-auth-title">Create your account</h2>
//         <p className="pp-auth-sub">It only takes a minute.</p>

//         <form onSubmit={handleSubmit} className="pp-form-grid">
//           <div className="pp-field">
//             <label htmlFor="name">Name</label>
//             <input
//               id="name"
//               className="pp-input"
//               type="text"
//               name="name"
//               value={formData.name}
//               onChange={handleChange}
//               placeholder="Your Name"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="email">Email</label>
//             <input
//               id="email"
//               className="pp-input"
//               type="email"
//               name="email"
//               value={formData.email}
//               onChange={handleChange}
//               placeholder="you@example.com"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="password">Password</label>
//             <input
//               id="password"
//               className="pp-input"
//               type="password"
//               name="password"
//               value={formData.password}
//               onChange={handleChange}
//               placeholder="Create a strong password"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="password">Confirm Password</label>
//             <div className="passwordWarning">
//               {passwordMismatch === null ? "" : passwordMismatch ? "✅" : "Password does not match!"}
//             </div>
//             <input
//               id="cpassword"
//               className="pp-input"
//               type="password"
//               name="cpassword"
//               onChange={handleConfirmChange}
//               placeholder="Confirm Password"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="panCardNumber">Pan Card Number</label>
//             <input
//               id="panCardNumber"
//               className="pp-input"
//               type="text"
//               name="panCardNumber"
//               value={formData.panCardNumber}
//               onChange={handleChange}
//               placeholder="ABC123456"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="bankAccountNumber">Bank Account Number</label>
//             <input
//               id="bankAccountNumber"
//               className="pp-input"
//               type="text"
//               name="bankAccountNumber"
//               value={formData.bankAccountNumber}
//               onChange={handleChange}
//               placeholder="XXXXXXXXXXXX"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="ifscCode">IFSC Code</label>
//             <input
//               id="ifscCode"
//               className="pp-input"
//               type="text"
//               name="ifscCode"
//               value={formData.ifscCode}
//               onChange={handleChange}
//               placeholder="XXXXXXX"
//               required
//             />
//           </div>

//           <div className="pp-field">
//             <label htmlFor="bankingPartner">Banking Partner</label>
//             <select 
//               id="bankingPartner"
//               className="pp-input"
//               name="bankingPartner"
//               value={formData.bankingPartner}
//               onChange={handleChange}
//               required>
//               <option value="">--Select--</option>
//               <option value="hdfc">HDFC</option>
//               <option value="sbi">SBI</option>
//               <option value="pnb">PNB</option>
//             </select>
//           </div>

//           <button type="submit" className="pp-btn pp-btn--primary pp-btn--block" disabled={loading}>
//             {loading ? "Creating…" : "Create Account"}
//           </button>
//         </form>

//         <div className="pp-sep"><span>or</span></div>

//         <Link to="/login" className="pp-btn pp-btn--outline pp-btn--block">
//           Log In
//         </Link>

//         <div className="text-center mt-3">
//           <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default Signup;


import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "./landing.css";

const isPasswordValid = (password) =>
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(password);

const isPanValid = (pan) => /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/.test(pan);

const isIfscValid = (ifsc) => /^[A-Z]{4}0[A-Z0-9]{6}$/.test(ifsc);

const isAccountValid = (acct) => /^[0-9]{9,18}$/.test(acct);

const isEmailValid = (email) => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!regex.test(email)) return false;

  const allowedDomains = ["gmail.com", "yahoo.com", "outlook.com", "natwest.com"];
  const domain = email.split("@")[1]?.toLowerCase();
  return allowedDomains.includes(domain);
};

const Signup = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    panCardNumber: "",
    bankAccountNumber: "",
    ifscCode: "",
    bankingPartner: "",
  });

  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const passwordMismatch =
    confirmPassword.length === 0 ? null : confirmPassword === formData.password;

  const handleChange = (e) => {
    const { name, value } = e.target;
    let v = value;

    if (name === "panCardNumber") v = v.replace(/\s+/g, "").toUpperCase();
    if (name === "ifscCode") v = v.replace(/\s+/g, "").toUpperCase();
    if (name === "bankAccountNumber") v = v.replace(/\s+/g, "");

    setFormData((prev) => ({ ...prev, [name]: v }));
  };

  const handleConfirmChange = (e) => setConfirmPassword(e.target.value);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isEmailValid(formData.email)) {
      toast.error("Invalid or unauthorized email domain.");
      return;
    }

    if (!isPasswordValid(formData.password)) {
      toast.error(
        "Password must be 8+ chars and include uppercase, lowercase, number, and special character."
      );
      return;
    }

    if (passwordMismatch === false || passwordMismatch === null) {
      toast.error("Passwords do not match.");
      return;
    }

    if (!isPanValid(formData.panCardNumber)) {
      toast.error("Invalid PAN (e.g., ABCDE1234F).");
      return;
    }

    if (!isIfscValid(formData.ifscCode)) {
      toast.error("Invalid IFSC (e.g., SBIN0001234).");
      return;
    }

    if (!isAccountValid(formData.bankAccountNumber)) {
      toast.error("Bank A/C must be 9–18 digits.");
      return;
    }

    if (loading) return;
    setLoading(true);

    try {
      const payload = {
        ...formData,
        panCardNumber: formData.panCardNumber.toUpperCase(),
        ifscCode: formData.ifscCode.toUpperCase(),
      };

      await axios.post("http://localhost:9090/api/user/signup", payload);
      toast.success("Signup successful! Redirecting to login…");
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      console.error("Signup failed:", err);
      toast.error(
        "Signup failed: " +
          (err.response?.data?.message || err.response?.data || err.message)
      );
    } finally {
      setLoading(false);
    }
  };

  const emailHintVisible = formData.email.length > 0 && !isEmailValid(formData.email);
  const pwHintVisible = formData.password.length > 0 && !isPasswordValid(formData.password);
  const panHintVisible = formData.panCardNumber.length > 0 && !isPanValid(formData.panCardNumber);
  const ifscHintVisible = formData.ifscCode.length > 0 && !isIfscValid(formData.ifscCode);
  const acctHintVisible =
    formData.bankAccountNumber.length > 0 && !isAccountValid(formData.bankAccountNumber);

  return (
    <div className="pp-auth-wrap">
      <div className="pp-card pp-auth-card position-relative">
        <div className="pp-auth-logo">
          Pay<span>Pilot</span>
        </div>
        <h2 className="pp-auth-title">Create your account</h2>
        <p className="pp-auth-sub">It only takes a minute.</p>

        <form onSubmit={handleSubmit} className="pp-form-grid">
          <div className="pp-field">
            <label htmlFor="name">Name</label>
            <input
              id="name"
              className="pp-input"
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Your Name"
              required
            />
          </div>

          <div className="pp-field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              className="pp-input"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="you@example.com"
              required
            />
            {emailHintVisible && (
              <div className="text-danger small mt-1">
                Invalid email or domain not allowed (e.g., gmail.com, yahoo.com, outlook.com, natwest.com).
              </div>
            )}
          </div>

          <div className="pp-field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              className="pp-input"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Create a strong password"
              required
              autoComplete="new-password"
            />
            {pwHintVisible && (
              <div className="text-danger small mt-1">
                Must include 8+ chars, uppercase, lowercase, number & special char.
              </div>
            )}
          </div>

          <div className="pp-field">
            <label htmlFor="cpassword">Confirm Password</label>
            <input
              id="cpassword"
              className="pp-input"
              type="password"
              name="cpassword"
              value={confirmPassword}
              onChange={handleConfirmChange}
              placeholder="Confirm Password"
              required
              autoComplete="new-password"
            />
            <div className="passwordWarning small mt-1">
              {passwordMismatch === null
                ? ""
                : passwordMismatch
                ? "✅ Passwords match"
                : "❌ Passwords do not match"}
            </div>
          </div>

          <div className="pp-field">
            <label htmlFor="panCardNumber">PAN</label>
            <input
              id="panCardNumber"
              className="pp-input"
              type="text"
              name="panCardNumber"
              value={formData.panCardNumber}
              onChange={handleChange}
              placeholder="ABCDE1234F"
              required
              inputMode="latin"
              maxLength={10}
            />
            {panHintVisible && (
              <div className="text-danger small mt-1">
                Format: 5 characters, 4 digits, 1 character (e.g., ABCDE1234F)
              </div>
            )}
          </div>

          <div className="pp-field">
            <label htmlFor="bankAccountNumber">Bank Account Number</label>
            <input
              id="bankAccountNumber"
              className="pp-input"
              type="text"
              name="bankAccountNumber"
              value={formData.bankAccountNumber}
              onChange={handleChange}
              placeholder="9–18 digits"
              required
              inputMode="numeric"
              maxLength={18}
            />
            {acctHintVisible && (
              <div className="text-danger small mt-1">Enter 9–18 digits only.</div>
            )}
          </div>

          <div className="pp-field">
            <label htmlFor="ifscCode">IFSC Code</label>
            <input
              id="ifscCode"
              className="pp-input"
              type="text"
              name="ifscCode"
              value={formData.ifscCode}
              onChange={handleChange}
              placeholder="SBIN0001234"
              required
              inputMode="latin"
              maxLength={11}
            />
            {ifscHintVisible && (
              <div className="text-danger small mt-1">
                Format: AAAA0XXXXXX (e.g., SBIN0001234)
              </div>
            )}
          </div>

          <div className="pp-field">
            <label htmlFor="bankingPartner">Banking Partner</label>
            <select
              id="bankingPartner"
              className="pp-input"
              name="bankingPartner"
              value={formData.bankingPartner}
              onChange={handleChange}
              required
            >
              <option value="">--Select--</option>
              <option value="hdfc">HDFC</option>
              <option value="sbi">SBI</option>
              <option value="pnb">PNB</option>
            </select>
          </div>

          <button
            type="submit"
            className="pp-btn pp-btn--primary pp-btn--block"
            disabled={loading}
          >
            {loading ? "Creating…" : "Create Account"}
          </button>
        </form>

        <div className="pp-sep">
          <span>or</span>
        </div>

        <Link to="/login" className="pp-btn pp-btn--outline pp-btn--block">
          Log In
        </Link>

        <div className="text-center mt-3">
          <Link to="/" className="pp-btn pp-btn--ghost pp-btn--sm">
            ← Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Signup;
