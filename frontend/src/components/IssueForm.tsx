import { useState } from "react";
import type { IssueCreateRequest, IssuePriority } from "../types/issue";

interface IssueFormProps {
  onSubmit: (request: IssueCreateRequest) => void;
  onCancel: () => void;
}

function IssueForm({ onSubmit, onCancel }: IssueFormProps) {
  const [projectId, setProjectId] = useState(1);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<IssuePriority>("MAJOR");

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!title.trim()) {
      alert("제목을 입력하세요.");
      return;
    }

    if (!description.trim()) {
      alert("설명을 입력하세요.");
      return;
    }

    onSubmit({
      projectId,
      title: title.trim(),
      description: description.trim(),
      priority,
      reporterId: 6,
    });
  };

  return (
    <form className="issue-create-form" onSubmit={handleSubmit}>
      <label className="create-form-field">
        <span>프로젝트</span>
        <select
          value={projectId}
          onChange={(event) => setProjectId(Number(event.target.value))}
        >
          <option value={1}>project1</option>
        </select>
      </label>

      <label className="create-form-field">
        <span>제목</span>
        <input
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          placeholder="이슈 제목을 입력하세요"
        />
      </label>

      <label className="create-form-field">
        <span>설명</span>
        <textarea
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          placeholder="이슈 내용을 입력하세요"
        />
      </label>

      <label className="create-form-field">
        <span>우선순위</span>
        <select
          value={priority}
          onChange={(event) => setPriority(event.target.value as IssuePriority)}
        >
          <option value="BLOCKER">BLOCKER</option>
          <option value="CRITICAL">CRITICAL</option>
          <option value="MAJOR">MAJOR</option>
          <option value="MINOR">MINOR</option>
          <option value="TRIVIAL">TRIVIAL</option>
        </select>
      </label>

      <div className="issue-create-actions">
        <button type="button" className="secondary-button" onClick={onCancel}>
          취소
        </button>
        <button type="submit" className="primary-button">
          저장
        </button>
      </div>
    </form>
  );
}

export default IssueForm;