import { useEffect, useState } from "react";
import { getProjects } from "../api/projectApi";
import type { IssueCreateRequest, IssuePriority } from "../types/issue";
import type { Project } from "../types/project";

type IssueFormRequest = Omit<IssueCreateRequest, "reporterId">;

interface IssueFormProps {
  onSubmit: (request: IssueFormRequest) => void;
  onCancel: () => void;
}

function IssueForm({ onSubmit, onCancel }: IssueFormProps) {
  const [projects, setProjects] = useState<Project[]>([]);
  const [projectId, setProjectId] = useState<number | "">("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<IssuePriority>("MAJOR");
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);

  useEffect(() => {
    getProjects()
      .then((data) => {
        setProjects(data);

        if (data.length > 0) {
          setProjectId(data[0].id);
        }
      })
      .catch((error) => {
        console.error("프로젝트 목록 조회 실패:", error);
        alert("프로젝트 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        setIsLoadingProjects(false);
      });
  }, []);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (projectId === "") {
      alert("프로젝트를 선택하세요.");
      return;
    }

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
    });
  };

  return (
    <form className="issue-create-form" onSubmit={handleSubmit}>
      <label className="create-form-field">
        <span>프로젝트</span>
        <select
          value={projectId}
          onChange={(event) => setProjectId(Number(event.target.value))}
          disabled={isLoadingProjects || projects.length === 0}
        >
          {isLoadingProjects && (
            <option value="">프로젝트 불러오는 중...</option>
          )}

          {!isLoadingProjects && projects.length === 0 && (
            <option value="">등록된 프로젝트가 없습니다</option>
          )}

          {projects.map((project) => (
            <option key={project.id} value={project.id}>
              {project.name}
            </option>
          ))}
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
        <button
          type="submit"
          className="primary-button"
          disabled={isLoadingProjects || projects.length === 0}
        >
          저장
        </button>
      </div>
    </form>
  );
}

export default IssueForm;