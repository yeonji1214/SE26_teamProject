import { useState } from "react";

interface ProjectFormRequest {
  name: string;
  description: string;
}

interface ProjectFormProps {
  onSubmit: (request: ProjectFormRequest) => void;
}

function ProjectForm({ onSubmit }: ProjectFormProps) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!name.trim()) {
      alert("프로젝트명을 입력하세요.");
      return;
    }

    onSubmit({
      name: name.trim(),
      description: description.trim(),
    });

    setName("");
    setDescription("");
  };

  return (
    <form className="project-form" onSubmit={handleSubmit}>
      <h3>프로젝트 추가</h3>

      <div className="project-form-body">
        <label className="project-form-field">
          <span>프로젝트명</span>
          <input
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="프로젝트명을 입력하세요"
          />
        </label>

        <label className="project-form-field">
          <span>설명(선택)</span>
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="프로젝트 설명을 입력하세요"
          />
        </label>
      </div>

      <div className="project-form-actions">
        <button
          type="button"
          className="secondary-button"
          onClick={() => {
            setName("");
            setDescription("");
          }}
        >
          취소
        </button>
        <button type="submit" className="primary-button">
          저장
        </button>
      </div>
    </form>
  );
}

export default ProjectForm;