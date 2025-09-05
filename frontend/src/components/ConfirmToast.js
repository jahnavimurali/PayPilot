import { toast } from "react-toastify";

export const ConfirmToast = (message, onConfirm, onCancel) => {
  toast(
    ({ closeToast }) => (
      <div style={{ textAlign: "center" }}>
        <p>{message}</p>
        <div style={{ display: "flex", justifyContent: "center", gap: "10px" }}>
          <button
            onClick={() => {
              onConfirm();
              closeToast();
            }}
            style={{
              padding: "5px 10px",
              background: "green",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer"
            }}
          >
            Yes
          </button>
          <button
            onClick={() => {
              if (onCancel) onCancel();
              closeToast();
            }}
            style={{
              padding: "5px 10px",
              background: "red",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer"
            }}
          >
            No
          </button>
        </div>
      </div>
    ),
    { autoClose: false }
  );
};
